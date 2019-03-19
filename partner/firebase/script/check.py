#!/usr/bin/python

import json
import sys
import os.path


def is_json(myjson):
  try:
    json_object = json.load(myjson)
  except ValueError, e:
  	raise ValueError('Invalid JSON format')
  return True


if len(sys.argv) != 2:
    raise ValueError('Should have at least one paramter')

file_path = sys.argv[1]

if not os.path.isfile(file_path):
   raise ValueError('Paramter should a file')

file = open(file_path, "r")

if is_json(file):
	print "JSON format checked"


