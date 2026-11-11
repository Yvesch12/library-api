"""Check browser-measured page boundaries and generated PDF page count."""
from html.parser import HTMLParser
from pathlib import Path
import re

class Pages(HTMLParser):
    def __init__(self):
        super().__init__()
        self.measurements = []

    def handle_starttag(self, tag, attrs):
        data = dict(attrs)
        if 'data-content-bottom' in data:
            self.measurements.append((float(data['data-content-bottom']), float(data['data-footer-top'])))

parser = Pages()
parser.feed(Path('target/pdf-layout.html').read_text(encoding='utf-8'))
print('Content bottom / footer top:', parser.measurements)
assert len(parser.measurements) == 6, 'Expected six browser-measured pages'
assert all(bottom < footer - 8 for bottom, footer in parser.measurements), 'Content overlaps footer'
pdf = Path('reports/reference-lab-submission.pdf').read_bytes()
pages = len(re.findall(rb'/Type\s*/Page\b', pdf))
print('PDF pages:', pages)
assert pages == 6, 'Unexpected PDF pagination'
assert pdf.startswith(b'%PDF-'), 'Invalid PDF header'
print('PDF header, pagination, and page-content boundaries passed.')
