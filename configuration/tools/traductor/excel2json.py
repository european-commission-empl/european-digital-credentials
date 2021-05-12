#!/usr/bin/env python3
import xlrd
from collections import OrderedDict
import simplejson as json
import tkinter as tk
from tkinter import filedialog

def myprint(arr):
	word = row_values[1]
	translate = translations_list.setdefault(arr[0], {})
	for i in range(1, len(arr)):
		translate = translate.setdefault(arr[i], word if i == (len(arr)-1) else {})


root = tk.Tk()
root.withdraw()

file_path = filedialog.askopenfilename()

# Open the workbook and select the first worksheet
wb = xlrd.open_workbook(file_path)
sh = wb.sheet_by_index(0)
# List to hold dictionaries
translations_list = {}
# Iterate through each row in worksheet and fetch values into dict
for rownum in range(1, sh.nrows):
    translations = OrderedDict()
    row_values = sh.row_values(rownum)
    arr = row_values[0].split('.')

    myprint(arr)

# Serialize the list of dicts to JSON
j = json.dumps(translations_list)
# Write to file
with open('data.json', 'w') as f:
	print('JSON created correctly!')
	f.write(j)