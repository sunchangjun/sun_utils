package com.entity;

/**
 * @author ：suncj
 * @date ：2019/9/12 15:30
 * 使用:	Collections.sort(List<EntrtySort>);
 */
public class EntrtySort implements Comparable<EntrtySort>{

        private String rid;
        private String name;
        private int play_user=1;
        private int play_count=1;
        private int play_time=0;

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPlay_user() {
            return play_user;
        }

        public void addPlay_user() {
            this.play_user++;
        }

        public int getPlay_count() {
            return play_count;
        }

        public void addPlay_count() {
            this.play_count++;
        }

        public int getPlay_time() {
            return play_time;
        }

        public void addPlay_time(int playTime) {
            this.play_time += playTime;
        }

        @Override
        public int compareTo(EntrtySort o) {
            if(this.play_count>o.getPlay_count()){
                return -1;
            }else if(this.play_count<o.getPlay_count()){
                return 1;
            }else{
                return 0;
            }
        }
    }

