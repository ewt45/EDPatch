/*
 * Copyright 2022 Google LLC
 * SPDX-License-Identifier: MIT
 */

#ifndef DRM_UTIL_H_
#define DRM_UTIL_H_

#pragma GCC diagnostic ignored "-Wgnu-zero-variadic-macro-arguments"
#pragma GCC diagnostic ignored "-Wmissing-field-initializers"
#pragma GCC diagnostic ignored "-Wlanguage-extension-token"
#pragma GCC diagnostic ignored "-Wgnu-statement-expression"

#include "linux/overflow.h"

void _drm_log(const char *fmt, ...);
#define drm_log(fmt, ...) _drm_log("%s:%d: " fmt, __func__, __LINE__, ##__VA_ARGS__)

#if 0
#define drm_dbg drm_log
#else
#define drm_dbg(fmt, ...)                                                                \
   do {                                                                                  \
   } while (0)
#endif

#define VOID2U64(x) ((uint64_t)(unsigned long)(x))
#define U642VOID(x) ((void *)(unsigned long)(x))

#ifndef NSEC_PER_SEC
#define NSEC_PER_SEC 1000000000ull
#endif

#endif /* DRM_UTIL_H_ */
