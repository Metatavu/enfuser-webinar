package fi.metatavu.enfuserwebinar;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * Example class for describing how to read data arrays from Enfuser NetCDF files
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class PrintVariableNames implements AutoCloseable {
  
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
  public PrintVariableNames(String location) throws IOException {
    netcdfFile = NetcdfFile.open(location);
  }

  /**
   * Main method
   * 
   * @param args program arguments
   * @throws Exception thrown when reading fails
   */
  public static void main(String[] args) throws Exception {
    try (PrintVariableNames example = new PrintVariableNames("file.nc")) {
      example.getVariableNames().stream().forEach(System.out::println);
    }
  }
  
  /**
   * Lists variable names from Enfuser NetCDF file
   * 
   * @return variable names from Enfuser NetCDF file
   */
  private List<String> getVariableNames() {
    return netcdfFile.getVariables().stream()
      .map(Variable::getShortName)
      .collect(Collectors.toList()); 
  }
  
  /**
   * AutoCloseable close method. Closes the NetCDF file
   */
  @Override
  public void close() throws Exception {
    this.netcdfFile.close();
  }

}
