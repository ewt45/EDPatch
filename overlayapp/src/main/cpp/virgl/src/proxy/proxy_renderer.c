/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "proxy_common.h"

#include "proxy_client.h"
#include "proxy_renderer.h"
#include "proxy_server.h"

int
proxy_renderer_init(const struct proxy_renderer_cbs *cbs, uint32_t flags)
{
   proxy_renderer.cbs = cbs;
   proxy_renderer.flags = flags;

   proxy_renderer.server = proxy_server_create();
   if (!proxy_renderer.server)
      goto fail;

   proxy_renderer.client =
      proxy_client_create(proxy_renderer.server, proxy_renderer.flags);
   if (!proxy_renderer.client)
      goto fail;

   return 0;

fail:
   proxy_renderer_fini();
   return -1;
}

void
proxy_renderer_fini(void)
{
   if (proxy_renderer.server)
      proxy_server_destroy(proxy_renderer.server);

   if (proxy_renderer.client)
      proxy_client_destroy(proxy_renderer.client);

   memset(&proxy_renderer, 0, sizeof(struct proxy_renderer));
}

void
proxy_renderer_reset(void)
{
   proxy_client_reset(proxy_renderer.client);
}
