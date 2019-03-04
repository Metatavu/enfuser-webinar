package fi.metatavu.enfuserwebinar;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.units.DateUnit;

/**
 * Example class for describing how to read data arrays from Enfuser NetCDF files
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class DataArrays implements AutoCloseable {
  
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
  public DataArrays(String location) throws IOException {
    netcdfFile = NetcdfFile.open(location);
  }

  /**
   * Main method
   * 
   * @param args program arguments
   * @throws Exception thrown when reading fails
   */
  public static void main(String[] args) throws Exception {
    int timeIndex = 1;
    int latIndex = 0;
    int lonIndex = 431;
    
    try (DataArrays example = new DataArrays("file.nc")) {
      OffsetDateTime time = example.getTime(timeIndex);
      float lat = example.getLat(latIndex);
      float lon = example.getLon(lonIndex);
      float aqi = example.getComponentValue("index_of_airquality_194", timeIndex, latIndex, lonIndex);
      System.out.println(String.format("Air quality index is %f at time %s, latitude %f, longitude %f", aqi, time.atZoneSameInstant(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME), lat, lon));
    }
  }
  
  /**
   * Returns value from three dimensional data array
   * 
   * @param variableName variable's name
   * @param timeIndex time index
   * @param latIndex latitude index
   * @param lonIndex longitude index
   * @return value
   * @throws Exception thrown when reading fails
   */
  private float getComponentValue(String variableName, int timeIndex, int latIndex, int lonIndex) throws Exception {
    Variable variable = getVariable(variableName);
    int[] origin = new int[] { timeIndex, latIndex, lonIndex };
    ArrayFloat.D3 array = (ucar.ma2.ArrayFloat.D3) variable.read(origin, new int[] {1, 1, 1});
    return array.get(0, 0, 0);
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
   * Returns latitude value from the NetCDF file
   * 
   * @param index latitude index
   * @return latitude value from the NetCDF file
   * @throws Exception thrown when lat resolving fails
   */
  public float getLat(int index) throws Exception {
    // Retrieve variable from the file
    Variable latVariable = getVariable("lat");
    
    // Read values as an array
    ArrayFloat.D1 latValues = getFloatArray(latVariable);
    
    // Read value from specified index
    return latValues.get(index);
  }
  
  /**
   * Returns longitude value from the NetCDF file
   * 
   * @param index longitude index
   * @return longitude value from the NetCDF file
   * @throws Exception thrown when longitude resolving fails
   */
  public float getLon(int index) throws Exception {
    // Retrieve variable from the file
    Variable lonVariable = getVariable("lon");
    
    // Read values as an array
    ArrayFloat.D1 lonValues = getFloatArray(lonVariable);
    
    // Read value from specified index
    return lonValues.get(index);
  }

  /**
   * Reads float array from file
   * 
   * @return float array
   * @throws Exception thrown when array reading fails
   */
  public ArrayFloat.D1 getFloatArray(Variable variable) throws Exception {
    return (ArrayFloat.D1) variable.read(null, variable.getShape());
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
