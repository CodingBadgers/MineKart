package uk.thecodingbadgers.minekart.userstats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import uk.thecodingbadgers.bDatabaseManager.bDatabaseManager;
import uk.thecodingbadgers.bDatabaseManager.bDatabaseManager.DatabaseType;
import uk.thecodingbadgers.bDatabaseManager.Database.BukkitDatabase;
import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTable;
import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class StatsManager {
	
	public class TimeResult {
		public String player;
		public String mount;
		public long time;
	}
	
	/**
	 * 
	 */
	private BukkitDatabase m_database;
	
	/**
	 * 
	 */
	private DatabaseTable m_courseTimesTable;
	
	/**
	 * Class constructor
	 */
	public StatsManager() {
		setupDatabase();
	}

	/**
	 * Setup the database and tables
	 */
	private void setupDatabase() {
		m_database = bDatabaseManager.createDatabase("MineKart-UserStats", MineKart.getInstance(), DatabaseType.SQLite);
		if (m_database == null) {
			MineKart.getInstance().getLogger().log(Level.WARNING, "Failed to setup database, User stats will be disabled!");
			return;
		}
		
		m_courseTimesTable = m_database.createTable("CourseTimes", CourseTimesData.class);
		if (m_courseTimesTable == null) {
			MineKart.getInstance().getLogger().log(Level.WARNING, "Failed to setup table, User stats will be disabled!");
			return;
		}
	}
	
	/**
	 * Log a course time
	 */
	public void logCourseTime(final String courseName, final String mountName, final String playerName, final long time) {
		
		CourseTimesData data = new CourseTimesData();
        data.courseName = courseName;
        data.mountName = mountName;
        data.playerName = playerName;
        data.time = time;
        
        if (!m_courseTimesTable.insert(data, CourseTimesData.class, false)) {
        	MineKart.getInstance().getLogger().log(Level.WARNING, "Failed to add course time to user stats!");
        	MineKart.getInstance().getLogger().log(Level.WARNING, playerName + ", " + mountName + ", " + courseName + ", " + time);
            return;
        }
        
	}

	/**
	 * Get a players times for a given course
	 * @param player The player to find
	 * @param course The course to find
	 * @return A list of times
	 */
	public List<Long> getPersonalTimes(Player player, Racecourse course) {
		
		List<Long> times = new ArrayList<Long>();
		
		ResultSet results = m_database.queryResult("SELECT * FROM CourseTimes WHERE playerName='" + player.getName() + "' AND courseName='" + course.getName() + "'");
		if (results == null) {
			return times;
		}
		
		try {
			while (results.next()) {
				times.add(results.getLong("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return times;
	}
	
	/**
	 * Get all the times for a given course
	 * @param course The course to find
	 * @return A list of time results
	 */
	public List<TimeResult> getCourseTimes(Racecourse course) {
		
		List<TimeResult> times = new ArrayList<TimeResult>();
		
		ResultSet results = m_database.queryResult("SELECT * FROM CourseTimes WHERE courseName='" + course.getName() + "'");
		if (results == null) {
			return times;
		}
		
		try {
			while (results.next()) {
				TimeResult time = new TimeResult();
				time.time = results.getLong("time");
				time.player = results.getString("playerName");
				time.mount = results.getString("mountName");
				times.add(time);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return times;
	}
	
}
