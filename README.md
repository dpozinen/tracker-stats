# tracker-stats

Service that listens for kafka messages containing stats about the sate of torrents on deluge (sent by [tracker-ops](https://github.com/dpozinen/tracker-ops))
and then stores them into `InfluxDB` which is then used by Grafana as a data source.

#### Why?

Because sometimes you just wanna see some cute little graphs 

<img width="1731" alt="Screenshot 2022-11-28 at 15 50 22" src="https://user-images.githubusercontent.com/33901469/204293977-3702fc1b-0466-4740-a925-09ee3aac9032.png">
