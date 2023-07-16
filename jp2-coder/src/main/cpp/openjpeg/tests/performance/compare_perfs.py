#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
# Copyright (c) 2017, IntoPIX SA
# Contact: support@intopix.com
# Author: Even Rouault
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS `AS IS'
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#

import sys


def Usage():
    print('Usage: compare_perfs.py [-noise_threshold val_in_pct]')
    print('                        [-warning_threshold val_in_pct]')
    print('                        [-error_threshold val_in_pct]')
    print('                        [-global_error_threshold val_in_pct]')
    print('                        ref.csv new.csv')
    sys.exit(1)

ref_filename = None
new_filename = None
noise_threshold = 2
warning_threshold = 4
error_threshold = 6
global_error_threshold = 2
i = 1
while i < len(sys.argv):
    if sys.argv[i] == '-noise_threshold' and i + 1 < len(sys.argv):
        i += 1
        noise_threshold = int(sys.argv[i])
    elif sys.argv[i] == '-warning_threshold' and i + 1 < len(sys.argv):
        i += 1
        warning_threshold = int(sys.argv[i])
    elif sys.argv[i] == '-error_threshold' and i + 1 < len(sys.argv):
        i += 1
        error_threshold = int(sys.argv[i])
    elif sys.argv[i] == '-global_error_threshold' and i + 1 < len(sys.argv):
        i += 1
        global_error_threshold = int(sys.argv[i])
    elif sys.argv[i][0] == '-':
        Usage()
    elif ref_filename is None:
        ref_filename = sys.argv[i]
    elif new_filename is None:
        new_filename = sys.argv[i]
    else:
        Usage()
    i += 1
if ref_filename is None or new_filename is None:
    Usage()

assert noise_threshold < warning_threshold
assert warning_threshold < error_threshold
assert global_error_threshold >= noise_threshold
assert global_error_threshold <= error_threshold

ref_lines = open(ref_filename, 'rt').readlines()[1:]
new_lines = open(new_filename, 'rt').readlines()[1:]
if len(ref_lines) != len(new_lines):
    raise Exception('files are not comparable')

ret_code = 0
for i in range(len(ref_lines)):
    line = ref_lines[i].replace('\n', '')
    filename_ref, num_iterations_ref, num_threads_ref, command_ref, \
        _, time_ms_ref = line.split(',')
    line = new_lines[i].replace('\n', '')
    filename_new, num_iterations_new, num_threads_new, command_new, \
        _, time_ms_new = line.split(',')
    assert filename_ref == filename_new
    assert num_iterations_ref == num_iterations_new
    assert num_threads_ref == num_threads_new
    assert command_ref == command_new
    time_ms_ref = int(time_ms_ref)
    time_ms_new = int(time_ms_new)
    if filename_ref == 'TOTAL':
        display = 'TOTAL'
    else:
        display = '%s, %s iterations, %s threads, %s' % \
            (filename_ref, num_iterations_ref, num_threads_ref, command_ref)
    display += ': ref_time %d ms, new_time %d ms' % (time_ms_ref, time_ms_new)
    var_pct = 100.0 * (time_ms_new - time_ms_ref) / time_ms_ref
    if abs(var_pct) <= noise_threshold:
        display += ', (stable) %0.1f %%' % var_pct
    elif var_pct < 0:
        display += ', (improvement) %0.1f %%' % var_pct
    else:
        display += ', (regression) %0.1f %%' % var_pct
    if filename_ref == 'TOTAL' and var_pct > global_error_threshold:
        display += ', ERROR_THRESHOLD'
        ret_code = 1
    elif var_pct > error_threshold:
        display += ', ERROR_THRESHOLD'
        ret_code = 1
    elif var_pct > warning_threshold:
        display += ', WARNING_THRESHOLD'
    print(display)

sys.exit(ret_code)
