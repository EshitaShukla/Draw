import os
import numpy as np
import sys
from PIL import Image
import PIL.ImageOps

number_images = 1000; # Number of images in each category
npy_dir = './raw/'
out_dir = './out/'
npy_files = [f for f in os.listdir(npy_dir) if os.path.isfile(os.path.join(npy_dir, f))]
print(npy_files)

categories = []

for x in npy_files:
	category_split = x.split('.')
	category = category_split[0].title()
	categories.append(category)
	
print(categories)

for y in categories:
	if not os.path.exists(os.path.join(out_dir, y)):
		os.makedirs(os.path.join(out_dir, y))

index_cat = 0		
for z in npy_files:
	print('Processing file', z)
	images = np.load(os.path.join(npy_dir, z))
	print('Saving in', categories[index_cat])
	number_imgs = range(0, number_images, 1)
	for a in number_imgs:
		print('Processing Image', a+1)
		file_name = '%s.jpg' % (a+1)
		file_path = os.path.join(out_dir, categories[index_cat], file_name)
		img = images[a].reshape(28,28)
		f_img = Image.fromarray(img)
		inverted_image = PIL.ImageOps.invert(f_img)
		inverted_image.save(file_path, 'JPEG')
	index_cat = index_cat + 1