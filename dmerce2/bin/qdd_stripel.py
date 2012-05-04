#!/usr/bin/env python

import sys
import os

fn_in = sys.argv[1]
fn_out = sys.argv[1] + '.new'
fd_in = open(fn_in, 'r')
fd_out = open(fn_out, 'w')
fd_in_lines = fd_in.readlines()
for line in fd_in_lines:
	if len(line) > 2:
		fd_out.write(line)
fd_in.close()
fd_out.close()
os.rename(fn_in, fn_in + '.old')
os.rename(fn_out, fn_in)
