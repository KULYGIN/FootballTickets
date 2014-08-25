package ServiceLayer;

import BLogic.Match;
import DataLayer.DBProcessor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class MatchTimeProcessor {
    public static ArrayList<Match> getAvalibleMatch (int searchType, String stringToSearch, Timestamp thisTime) {
        Timestamp nowTime = getNowTime();
        return DBProcessor.getMatchAfterTime(nowTime, searchType, stringToSearch, thisTime);
    }

    public static Timestamp getNowTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }
}
