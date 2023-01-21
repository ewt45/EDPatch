#############################################################################
#
# Copyright (C) 2019 Collabora Ltd
#
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the "Software"),
# to deal in the Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
# OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
# OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
# ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
# OTHER DEALINGS IN THE SOFTWARE.
#

virgl_test_server_sources = [
   'util.c',
   'util.h',
   'vtest_shm.c',
   'vtest_shm.h',
   'vtest_server.c',
   'vtest_renderer.c',
   'vtest_protocol.h',
   'vtest.h'
]


virgl_test_server = executable(
   'virgl_test_server',
   virgl_test_server_sources,
   dependencies : [libvirglrenderer_dep, gallium_dep],
   install : true
)

if with_fuzzer
   assert(cc.has_argument('-fsanitize=fuzzer'),
          'Fuzzer enabled but compiler does not support "-fsanitize=fuzzer"')

   vtest_obj = virgl_test_server.extract_objects(['util.c',
                                                  'vtest_shm.c',
                                                  'vtest_renderer.c'
                                                 ])

   vtest_fuzzer = executable(
      'vtest_fuzzer',
      'vtest_fuzzer.c',
      c_args : [ '-fsanitize=fuzzer' ],
      link_args : [ '-fsanitize=fuzzer' ],
      objects : vtest_obj,
      dependencies : libvirglrenderer_dep
   )
endif
