/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "proxy_common.h"

#include <stdarg.h>
#include <stdio.h>

struct proxy_renderer proxy_renderer;

void
proxy_log(const char *fmt, ...)
{
   const char prefix[] = "proxy: ";
   char line[1024];
   size_t len;
   va_list va;
   int ret;

   len = ARRAY_SIZE(prefix) - 1;
   memcpy(line, prefix, len);

   va_start(va, fmt);
   ret = vsnprintf(line + len, ARRAY_SIZE(line) - len, fmt, va);
   va_end(va);

   if (ret < 0) {
      const char log_error[] = "log error";
      memcpy(line + len, log_error, ARRAY_SIZE(log_error) - 1);
      len += ARRAY_SIZE(log_error) - 1;
   } else if ((size_t)ret < ARRAY_SIZE(line) - len) {
      len += ret;
   } else {
      len = ARRAY_SIZE(line) - 1;
   }

   /* make room for newline */
   if (len + 1 >= ARRAY_SIZE(line))
      len--;

   line[len++] = '\n';
   line[len] = '\0';

   virgl_log("%s", line);
}
