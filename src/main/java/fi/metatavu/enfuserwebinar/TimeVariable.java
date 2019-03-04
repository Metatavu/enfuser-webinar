package fi.metatavu.enfuserwebinar;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ucar.ma2.ArrayInt;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.units.DateUnit;

/**
 * Example class for describing how time variables work on Enfuser NetCDF files
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class TimeVariable implements AutoCloseable {
  
  /**
   * Private field for storing the NetCDF file object
   */
  private NetcdfFile netcdfFile;
 
  /**
   * Constructor for example class
   * 
   * @param location NetCDF file location
   * @throws IOException thrown when file opening fails
   */
  public TimeVariable(String location) throws IOException {
    netcdfFile = NetcdfFile.open(location);
  }

  /**
   * Main method
   * 
   * @param args program arguments
   * @throws Exception thrown when time reading fails
   */
  public static void main(String[] args) throws Exception {
    int timeIndex = 7;
    
    try (TimeVariable example = new TimeVariable("file.nc")) {
      System.out.println(String.format("%dth time index equals %s", timeIndex, example.getTime(timeIndex).format(DateTimeFormatter.ISO_DATE_TIME)));
    }
  }
  
  /**
   * Returns time value from the NetCDF file
   * 
   * @param time index
   * @return time value from the NetCDF file
   * @throws Exception thrown when time resolving fails
   */
  public OffsetDateTime getTime(int index) throws Exception {
    // Retrieve time variable from the file
    Variable timeVariable = getVariable("time");
    
    // Read time values as an array
    ArrayInt.D1 timeValues = getTimeArray(timeVariable);
    
    // Parse date unit from the variable
    DateUnit dateUnit = new DateUnit(timeVariable.getUnitsString());
    
    // Read time and return it as a offset date time
    Date date = dateUnit.makeDate(timeValues.getDouble(index));
    return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  /**
   * Reads time array from file
   * 
   * @return time array
   * @throws Exception thrown when time array reading fails
   */
  public ArrayInt.D1 getTimeArray(Variable timeVariable) throws Exception {
    return (ArrayInt.D1) timeVariable.read(null, timeVariable.getShape());
  }
  
  /**
   * AutoCloseable close method. Closes the NetCDF file
   */
  @Override
  public void close() throws Exception {
    this.netcdfFile.close();
  }
  
  /**
   * Finds variable by name
   * 
   * @param variableName variable name
   * @return variable or null if not found
   */
  private Variable getVariable(String variableName) {
    return netcdfFile.findVariable(variableName);
  }

}
