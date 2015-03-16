#!/bin/sh

# allow jetty to run as jetty on non-prileged port 8443 but take trafiic from port 443


/sbin/iptables -t nat -A OUTPUT -p tcp  --dport 443 -j  REDIRECT --to-port 8443
