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

import os
import subprocess
import sys
import time


def Usage():
    print('Usage: perf_test.py [-kakadu] [-i filelist.csv] [-o out.csv] [-q]')
    sys.exit(1)

opj_decompress_path = 'opj_decompress'
opj_compress_path = 'opj_compress'
kdu_expand_path = 'kdu_expand'
kdu_compress_path = 'kdu_compress'

in_filename = 'perf_test_filelist.csv'
out_filename = None
i = 1
quiet = False
kakadu = False
while i < len(sys.argv):
    if sys.argv[i] == '-o' and i + 1 < len(sys.argv):
        i += 1
        out_filename = sys.argv[i]
    elif sys.argv[i] == '-i' and i + 1 < len(sys.argv):
        i += 1
        in_filename = sys.argv[i]
    elif sys.argv[i] == '-q':
        quiet = True
    elif sys.argv[i] == '-kakadu':
        kakadu = True
    else:
        Usage()
    i += 1

i = 0
while i < 10 * 1024 * 1024:
    i += 1

out_file = None
if out_filename is not None:
    out_file = open(out_filename, 'wt')
    out_file.write('filename,iterations,threads,command,comment,time_ms\n')

total_time = 0
for line in open(in_filename, 'rt').readlines()[1:]:
    line = line.replace('\n', '')
    filename, num_iterations, num_threads, command, comment = line.split(',')
    num_threads = int(num_threads)
    num_iterations = int(num_iterations)
    start = time.time()
    for i in range(num_iterations):
        env = None
        if kakadu:
            if command == 'DECOMPRESS':
                args = [kdu_expand_path,
                        '-i', filename,
                        '-num_threads', str(num_threads),
                        '-o', 'out_perf_test.pgm']
            elif command == 'COMPRESS':
                args = [kdu_compress_path,
                        '-i', filename,
                        '-num_threads', str(num_threads),
                        'Creversible=yes',
                        '-o', 'out_perf_test.jp2']
            else:
                assert False, command
        else:
            env = os.environ
            if num_threads > 1:
                env['OPJ_NUM_THREADS'] = str(num_threads)
            else:
                env['OPJ_NUM_THREADS'] = '0'
            if command == 'DECOMPRESS':
                args = [opj_decompress_path,
                        '-i', filename, '-o', 'out_perf_test.pgm']
            elif command == 'COMPRESS':
                args = [opj_compress_path,
                        '-i', filename, '-o', 'out_perf_test.jp2']
            else:
                assert False, command
        p = subprocess.Popen(args,
                             stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE,
                             close_fds=True,
                             env=env)
        p.wait()
    stop = time.time()
    if os.path.exists('out_perf_test.pgm'):
        os.unlink('out_perf_test.pgm')
    if os.path.exists('out_perf_test.jp2'):
        os.unlink('out_perf_test.jp2')
    spent_time = stop - start
    total_time += spent_time
    if not quiet:
        if len(comment) != 0:
            print('%s (%s), %d iterations, %d threads, %s: %.02f s' %
                  (filename, comment, num_iterations, num_threads,
                   command, spent_time))
        else:
            print('%s, %d iterations, %d threads, %s: %.02f s' %
                  (filename, num_iterations, num_threads, command, spent_time))
    if out_file is not None:
        out_file.write('%s,%d,%d,%s,%s,%d\n' %
                       (filename, num_iterations, num_threads, command,
                        comment, spent_time * 1000))

if not quiet:
    print('Total time: %.02f s' % total_time)
if out_file is not None:
    out_file.write('%s,,,,,%d\n' % ('TOTAL', total_time * 1000))
