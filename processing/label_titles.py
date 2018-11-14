import sys

file_name = sys.argv[1]
lines = [line.rstrip('\n') for line in open(file_name)]
label_titles = []

for x in lines:
	a = x.title()
	label_titles.append(a)

with open('label_titles.txt', 'a') as the_file:
	for y in label_titles:
		title = '%s\n' % (y)
		the_file.write(title)
	