!/bin/bash
while [ 1 ]
do
killall udhcpd
ifconfig ath0 down
iwconfig ath0 mode managed essid SitecomBBE310 ap any auto commit
ifconfig ath0 up
udhcpc -b -i ath0
done