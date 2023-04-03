/**************************************************************************
 *
 * Copyright (C) 2015 Red Hat Inc.
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
#include <pthread.h>
#include <stdio.h>
#include <signal.h>
#include <stdbool.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/un.h>
#include <fcntl.h>
#include "vtest_old.h"
#include "vtest_protocol_old.h"

int vtest_open_socket_old(const char *path)
{
    int sock;

    if( !path ) path = "/tmp/.virgl_test";
    
	if( path[0] == ':' )
	{         
	    struct sockaddr_in in;

    	sock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP );
        if (sock < 0) {
			return -1;
	    }

	    memset(&in, 0, sizeof(in));
    	in.sin_family = AF_INET;
    	in.sin_port = htons(atoi(path + 1));
    
    	if (bind(sock, (struct sockaddr *)&in, sizeof(in)) < 0) {
			goto err;
		}
    }
    else
    {
	    struct sockaddr_un un;

	    sock = socket(PF_UNIX, SOCK_STREAM, 0);
    	if (sock < 0) {
		return -1;
    	}

    	memset(&un, 0, sizeof(un));
    	un.sun_family = AF_UNIX;
    
    	snprintf(un.sun_path, sizeof(un.sun_path), "%s", path);

	    unlink(un.sun_path);

    	if (bind(sock, (struct sockaddr *)&un, sizeof(un)) < 0) {
			goto err;
	    }
	    chmod(un.sun_path,0777);
	}
    
    if (listen(sock, 1) < 0){
	goto err;
    }

    return sock;
 err:
    close(sock);
    return -1;
}

int wait_for_socket_accept_old(int sock)
{
    fd_set read_fds;
    int new_fd;
    int ret;
    FD_ZERO(&read_fds);
    FD_SET(sock, &read_fds);

    ret = select(sock + 1, &read_fds, NULL, NULL, NULL);
    if (ret < 0)
	return ret;

    if (FD_ISSET(sock, &read_fds)) {	
	new_fd = accept(sock, NULL, NULL);
	return new_fd;
    }
    return -1;
}
#ifndef ANDROID_JNI
static void *renderer_thread(void *arg)
{
    int fd = *(int*)arg;
    static int ctx_id = 0;
    ctx_id++;

    run_renderer(fd, ctx_id);
    return NULL;
}

int main(int argc, char **argv)
{
    int ret, sock = -1, in_fd, out_fd;
    pid_t pid;
    bool do_fork = true, loop = true, threads = false;
    struct sigaction sa;

#ifdef __AFL_LOOP
while (__AFL_LOOP(1000)) {
#endif

   if (argc > 1) {
      if (!strcmp(argv[1], "--no-loop-or-fork")) {
        do_fork = false;
        loop = false;
      } else if (!strcmp(argv[1], "--no-fork")) {
	do_fork = false;
      } else if (!strcmp(argv[1], "--threads")) {
        do_fork = false;
        threads = true;
      } else {
         ret = open(argv[1], O_RDONLY);
         if (ret == -1) {
            perror(0);
            exit(1);
         }
         in_fd = ret;
         ret = open("/dev/null", O_WRONLY);
         if (ret == -1) {
            perror(0);
            exit(1);
         }
         out_fd = ret;
         loop = false;
         do_fork = false;
         goto start;
      }
    }

    if (do_fork) {
      sa.sa_handler = SIG_IGN;
      sigemptyset(&sa.sa_mask);
      sa.sa_flags = 0;
      if (sigaction(SIGCHLD, &sa, 0) == -1) {
	perror(0);
	exit(1);
      }
    }
#ifdef X11
  XInitThreads();
#endif
    sock = vtest_open_socket(getenv("VTEST_SOCK"));
restart:
    in_fd = wait_for_socket_accept(sock);
    out_fd = in_fd;

start:
    if (do_fork) {
      /* fork a renderer process */
      switch ((pid = fork())) {
      case 0:
        run_renderer(in_fd, 1);
	exit(0);
	break;
      case -1:
      default:
	close(in_fd);
        if (loop)
           goto restart;
      }
    } else if(threads)
    {
      pthread_t thread;
//      static int ctx_id;
//      ctx_id++;
//      void *d = create_renderer_old( in_fd, ctx_id);
      pthread_create(&thread, NULL, renderer_thread, &in_fd);
      goto restart;
    } else {
      run_renderer(in_fd, 1);
      if (loop)
         goto restart;
    }

    if (sock != -1)
       close(sock);
    if (in_fd != out_fd)
       close(out_fd);

#ifdef __AFL_LOOP
}
#endif
}
#endif