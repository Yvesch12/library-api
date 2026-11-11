"""Summarize real JaCoCo XML and a documented source-import coupling heuristic."""
import json
import re
import sys
from pathlib import Path
from xml.etree import ElementTree as ET

root = Path(__file__).resolve().parents[1]
sources = sorted((root / 'src/main/java').rglob('*.java'))
text_by_name = {p.stem: p.read_text(encoding='utf-8-sig') for p in sources}
packages = {p.stem: re.search(r'package\s+([\w.]+)', text_by_name[p.stem]).group(1) for p in sources}

def counter(element, kind):
    node = element.find(f"counter[@type='{kind}']")
    if node is None:
        return {'covered': 0, 'missed': 0, 'percent': None}
    covered, missed = int(node.get('covered')), int(node.get('missed'))
    return {'covered': covered, 'missed': missed,
            'percent': round(100 * covered / (covered + missed), 2) if covered + missed else None}

report = ET.parse(root / 'target/site/jacoco/jacoco.xml').getroot()
result = {'overall': {kind: counter(report, kind) for kind in ('LINE', 'BRANCH')}, 'classes': {}, 'services': {}}
for package in report.findall('package'):
    for cls in package.findall('class'):
        result['classes'][cls.get('name')] = counter(cls, 'LINE')
    if package.get('name').endswith('/service'):
        result['serviceCoverage'] = {kind: counter(package, kind) for kind in ('LINE', 'BRANCH')}

for p in sources:
    if p.parent.name != 'service':
        continue
    text = text_by_name[p.stem]
    imports = set(re.findall(r'^import\s+([\w.*]+);', text, re.M))
    expanded = set()
    for imported in imports:
        if not imported.endswith('.*'):
            expanded.add(imported)
            continue
        prefix = imported[:-2]
        candidates = {name for name, pkg in packages.items() if pkg == prefix}
        if prefix == 'java.util':
            candidates.update(('List', 'Map', 'Optional', 'NoSuchElementException', 'Set', 'ArrayList', 'Objects'))
        expanded.update(prefix + '.' + name for name in candidates if re.search(r'\b' + name + r'\b', text))
    callers = [name for name, other in text_by_name.items() if name != p.stem
               and re.search(r'\b' + p.stem + r'\b', re.sub(r'^import.*$', '', other, flags=re.M))]
    ce, ca = len(expanded), len(callers)
    result['services'][p.stem] = {'Ce': ce, 'Ca': ca, 'instability': round(ce / (ce + ca), 3) if ce + ca else 0,
                                'callers': callers}
result['source'] = {'javaFiles': len(sources), 'physicalLines': sum(len(t.splitlines()) for t in text_by_name.values()),
                    'nonblankLines': sum(sum(bool(line.strip()) for line in t.splitlines()) for t in text_by_name.values())}
destination = root / (sys.argv[1] if len(sys.argv) > 1 else 'docs/metrics.json')
destination.write_text(json.dumps(result, indent=2) + '\n', encoding='utf-8')
print(json.dumps({k: v for k, v in result.items() if k != 'classes'}, indent=2))
