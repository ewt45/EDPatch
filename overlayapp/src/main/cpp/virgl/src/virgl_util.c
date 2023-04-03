/**************************************************************************
 *
 * Copyright (C) 2019 Chromium.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
    **************************************************************************/

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "virgl_util.h"

#include <errno.h>
#ifdef HAVE_EVENTFD_H
#include <sys/eventfd.h>
#endif
#include <unistd.h>

#include "util/os_misc.h"
#include "util/u_pointer.h"

#include <assert.h>
#include <stdarg.h>
#include <stdio.h>

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#if ENABLE_TRACING == TRACE_WITH_PERFETTO
#include <vperfetto-min.h>
#endif

#if ENABLE_TRACING == TRACE_WITH_STDERR
#include <stdio.h>
#endif

unsigned hash_func_u32(void *key)
{
   intptr_t ip = pointer_to_intptr(key);
   return (unsigned)(ip & 0xffffffff);
}

int compare_func(void *key1, void *key2)
{
   if (key1 < key2)
      return -1;
   if (key1 > key2)
      return 1;
   else
      return 0;
}

bool has_eventfd(void)
{
#ifdef HAVE_EVENTFD_H
   return true;
#else
   return false;
#endif
}

int create_eventfd(unsigned int initval)
{
#ifdef HAVE_EVENTFD_H
   return eventfd(initval, EFD_CLOEXEC | EFD_NONBLOCK);
#else
   (void)initval;
   return -1;
#endif
}

int write_eventfd(int fd, uint64_t val)
{
   const char *buf = (const char *)&val;
   size_t count = sizeof(val);
   ssize_t ret = 0;

   while (count) {
      ret = write(fd, buf, count);
      if (ret < 0) {
         if (errno == EINTR)
            continue;
         break;
      }
      count -= ret;
      buf += ret;
   }

   return count ? -1 : 0;
}

void flush_eventfd(int fd)
{
    ssize_t len;
    uint64_t value;
    do {
       len = read(fd, &value, sizeof(value));
    } while ((len == -1 && errno == EINTR) || len == sizeof(value));
}

static
void virgl_default_logger(const char *fmt, va_list va)
{
   static FILE* fp = NULL;
   if (NULL == fp) {
      const char* log = getenv("VIRGL_LOG_FILE");
      if (log) {
         char *log_prefix = strdup(log);
         char *log_suffix = strstr(log_prefix, "%PID%");
         if (log_suffix) {
            *log_suffix = 0;
            log_suffix += 5;
            int len = strlen(log) + 32;
            char *name = malloc(len);
            snprintf(name, len, "%s%d%s", log_prefix, getpid(), log_suffix);
            fp = fopen(name, "a");
            free(name);
         } else {
            fp = fopen(log, "a");
         }
         free(log_prefix);
         if (NULL == fp) {
            fprintf(stderr, "Can't open %s\n", log);
            fp = stderr;
         }
      } else {
            fp = stderr;
      }
   }
   vfprintf(fp, fmt, va);
   fflush(fp);
}

static
void virgl_null_logger(UNUSED const char *fmt, UNUSED va_list va)
{
}

static virgl_debug_callback_type virgl_logger = virgl_default_logger;

virgl_debug_callback_type virgl_log_set_logger(virgl_debug_callback_type logger)
{
   virgl_debug_callback_type old = virgl_logger;

   /* virgl_null_logger is internal */
   if (old == virgl_null_logger)
      old = NULL;
   if (!logger)
      logger = virgl_null_logger;

   virgl_logger = logger;
   return old;
}

void virgl_logv(const char *fmt, va_list va)
{
   assert(virgl_logger);
   virgl_logger(fmt, va);
}

#if ENABLE_TRACING == TRACE_WITH_PERCETTO
PERCETTO_CATEGORY_DEFINE(VIRGL_PERCETTO_CATEGORIES)

void trace_init(void)
{
  PERCETTO_INIT(PERCETTO_CLOCK_DONT_CARE);
}
#endif

#if ENABLE_TRACING == TRACE_WITH_PERFETTO
static void on_tracing_state_change(bool enabled) {
    virgl_log("%s: tracing state change: %d\n", __func__, enabled);
}

void trace_init(void)
{
   struct vperfetto_min_config config = {
      .on_tracing_state_change = on_tracing_state_change,
      .init_flags = VPERFETTO_INIT_FLAG_USE_SYSTEM_BACKEND,
            .filename = NULL,
            .shmem_size_hint_kb = 32 * 1024,
   };

   vperfetto_min_startTracing(&config);
}

const char *trace_begin(const char *scope)
{
   vperfetto_min_beginTrackEvent_VMM(scope);
   return scope;
}

void trace_end(const char **dummy)
{
   (void)dummy;
   vperfetto_min_endTrackEvent_VMM();
}
#endif

#if ENABLE_TRACING == TRACE_WITH_STDERR
static int nesting_depth = 0;
void trace_init(void)
{
}

const char *trace_begin(const char *scope)
{
   for (int i = 0; i < nesting_depth; ++i)
      fprintf(stderr, "  ");

   fprintf(stderr, "ENTER:%s\n", scope);
   nesting_depth++;

   return scope;
}

void trace_end(const char **func_name)
{
   --nesting_depth;
   for (int i = 0; i < nesting_depth; ++i)
      fprintf(stderr, "  ");
   fprintf(stderr, "LEAVE %s\n", *func_name);
}
#endif
