package cn.maoookai;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class Speakstats extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

    private static final String driver = "org.mariadb.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String username = "root";
    private static final String password = "maokai";

    static String clearTodayData = "UPDATE speakstats.todaystats SET Bubbles = 0";

    static
    {
        try
        {
            Class.forName(driver);
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
    }

    private static Connection mysqlConnector = null;
    //单例模式返回数据库连接
    public static Connection getConnection() throws Exception
    {
        if(mysqlConnector == null)
        {
            mysqlConnector = DriverManager.getConnection(url, username, password);
        }
        return mysqlConnector;
    }

    static Statement mysqlStatement;

    static {
        try {
            mysqlStatement = getConnection().createStatement();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        CQ = new CQDebug();
        CQ.logInfo("Test","Successfully Started");
        Speakstats test = new Speakstats();
        test.startup();
        test.enable();
        //test.groupMsg(0,10060,933686208L,1220568034,"","龙王是谁",0);
        //dragonSetter();
        sqlExecutor(clearTodayData);
    }

    public static void sqlExecutor(String sql) throws SQLException {
        mysqlStatement.execute(sql);
    }

    public static void dragonSetter() throws SQLException {
        ResultSet dragon_2 = sqlQueryExecutor("select speakstats.todaystats.QQ from speakstats.todaystats where speakstats.todaystats.Bubbles = (select max(speakstats.todaystats.Bubbles)from speakstats.todaystats);");
        dragon_2.next();
        long currentDragon = dragon_2.getInt(1);
        Date currentDateTime = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDate.format(currentDateTime);
        System.out.println(currentDate);
        String setCurrentDragon = String.format("INSERT INTO speakstats.historydragon(date,dragon) VALUES('%s',%d)", currentDate, currentDragon);
        sqlExecutor(setCurrentDragon);
        dragon_2.close();
    }

    public static ResultSet sqlQueryExecutor(String sql) throws SQLException {
        return mysqlStatement.executeQuery(sql);
    }

    public static void speakProcessor(long fromQQ) throws SQLException {
        String addSpeakRecord = String.format("insert into speakstats.todaystats (QQ,Bubbles) select '%d','1' from dual where not exists(select QQ from speakstats.todaystats where QQ='%d');", fromQQ, fromQQ);
        String addSpeak = String.format("update speakstats.todaystats set Bubbles=Bubbles+1 where QQ = %d;", fromQQ);
        sqlExecutor(addSpeakRecord);
        sqlExecutor(addSpeak);
    }

    public static void dragonReporter(long fromGroup) throws SQLException {
        ResultSet dragon_1 = sqlQueryExecutor("select max(speakstats.todaystats.Bubbles)from speakstats.todaystats;");
        dragon_1.next();
        int dragonBubbles = dragon_1.getInt(1);
        dragon_1.close();
        ResultSet dragon_2 = sqlQueryExecutor("select speakstats.todaystats.QQ from speakstats.todaystats where speakstats.todaystats.Bubbles = (select max(speakstats.todaystats.Bubbles)from speakstats.todaystats);");
        dragon_2.next();
        long dragonQQ = dragon_2.getLong(1);
        CQ.sendGroupMsg(fromGroup, String.format("截止目前，今天的龙王是[CQ:at,qq=%d]，发言了%d次", dragonQQ,dragonBubbles));
        dragon_1.close();
        dragon_2.close();
    }

    public String appInfo() {
        String AppID = "cn.maoookai.speakstats";
        return CQAPIVER + "," + AppID;
    }

    public int startup() {
        return 0;
    }

    public int exit() {
        return 0;
    }

    public int enable() {
        return 0;
    }

    public int disable() {
        return 0;
    }

    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        if (msg.equals("清空每日数据")) {
            try {
                sqlExecutor(clearTodayData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return 0;
    }

    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        if (fromGroup==933686208L)
        {
            try {
                speakProcessor(fromQQ);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (msg.equals("龙王是谁")) {
                try {
                    dragonReporter(fromGroup);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public int discussMsg(int i, int i1, long l, long l1, String s, int i2) {
        return 0;
    }

    public int groupUpload(int i, int i1, long l, long l1, String s) {
        return 0;
    }

    public int groupAdmin(int i, int i1, long l, long l1) {
        return 0;
    }

    public int groupMemberDecrease(int i, int i1, long l, long l1, long l2) {
        return 0;
    }

    public int groupMemberIncrease(int i, int i1, long l, long l1, long l2) {
        return 0;
    }

    public int friendAdd(int i, int i1, long l) {
        return 0;
    }

    public int requestAddFriend(int i, int i1, long l, String s, String s1) {
        return 0;
    }

    public int requestAddGroup(int i, int i1, long l, long l1, String s, String s1) {
        return 0;
    }
}

class Cleaner extends TimerTask{

    String clearTodayData = "UPDATE speakstats.todaystats SET Bubbles = 0";

    public void run() {
        try {
            Speakstats.dragonReporter(933686208L);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Speakstats.sqlExecutor(clearTodayData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Speakstats.dragonSetter();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

class TimerManager {

    public static void main(String[] args) {
        new TimerManager();
    }

    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public TimerManager() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date date= calendar.getTime();
        if (date.before(new Date())) {
            date = this.addDay(date, 1);
        }
        Timer timer = new Timer();
        Cleaner cleaner = new Cleaner();
        timer.schedule(cleaner,date,PERIOD_DAY);
    }

    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

}