"""Run the two required deliberate bugs, save actual failures, always restore source."""
import json
import os
import subprocess
from pathlib import Path
from xml.etree import ElementTree as ET

root = Path(__file__).resolve().parents[1]
source = root / 'src/main/java/com/example/library/service/BorrowingService.java'
original = source.read_text(encoding='utf-8-sig')
environment = os.environ.copy()
environment['JAVA_HOME'] = r'C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot'
maven = str(Path.home() / 'tools/apache-maven-3.10.0/bin/mvn.cmd')
mutations = {
    'double-rate': original.replace('DAILY_FINE_RATE = new BigDecimal("0.50")', 'DAILY_FINE_RATE = new BigDecimal("1.00")'),
    'null-on-time': original.replace(
        'return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(daysOverdue(dueDate, returnDate)));',
        'return daysOverdue(dueDate, returnDate) == 0 ? null : DAILY_FINE_RATE.multiply(BigDecimal.valueOf(daysOverdue(dueDate, returnDate)));'),
}
results = {}
try:
    with (root / 'lab7-mutation-baseline.log').open('w', encoding='utf-8') as log:
        baseline = subprocess.run([maven, '--batch-mode', '--no-transfer-progress', 'test', '-Ddebug=false'],
                                  cwd=root, env=environment, stdout=log, stderr=subprocess.STDOUT)
    if baseline.returncode != 0:
        raise RuntimeError('Baseline must pass before a mutation experiment')
    for name, mutated in mutations.items():
        if mutated == original:
            raise RuntimeError('Mutation pattern not found: ' + name)
        source.write_text(mutated, encoding='utf-8')
        with (root / ('lab7-mutation-' + name + '.log')).open('w', encoding='utf-8') as log:
            completed = subprocess.run([maven, '--batch-mode', '--no-transfer-progress', 'test', '-Ddebug=false'],
                                       cwd=root, env=environment, stdout=log, stderr=subprocess.STDOUT)
        failed, passed = [], []
        for report in (root / 'target/surefire-reports').glob('TEST-*.xml'):
            for test in ET.parse(report).getroot().findall('testcase'):
                identifier = test.get('classname') + '.' + test.get('name')
                (failed if test.find('failure') is not None or test.find('error') is not None else passed).append(identifier)
        results[name] = {'exitCode': completed.returncode, 'failed': failed, 'passed': passed}
        print(name + ': exit=' + str(completed.returncode) + ', detected by ' + str(len(failed)) + ' tests', flush=True)
        if completed.returncode == 0 or not failed:
            raise RuntimeError('Mutation was not detected: ' + name)
finally:
    source.write_text(original, encoding='utf-8')
    (root / 'docs/lab7-mutations.json').write_text(json.dumps(results, indent=2) + '\n', encoding='utf-8')
    print('Original BorrowingService source restored.', flush=True)
