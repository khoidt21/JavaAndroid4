package model;

public class ModelMap {

      private String endAddress;
      private String startAddress;
      private String distance;
      private String duration;

      public ModelMap(){}
      public ModelMap(String endAddress, String startAddress, String distance, String duration){
           this.setEndAddress(endAddress);
           this.setStartAddress(startAddress);
           this.setDistance(distance);
           this.setDuration(duration);
      }

      public String getEndAddress() {
        return endAddress;
      }

      public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
      }

      public String getStartAddress() {
        return startAddress;
      }

      public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
      }

      public String getDistance() {
        return distance;
      }

      public void setDistance(String distance) {
        this.distance = distance;
      }

      public String getDuration() {
        return duration;
      }

      public void setDuration(String duration) {
        this.duration = duration;
      }


    @Override
    public String toString() {
        return "ModelMap{" +
                "endAdress=" + endAddress +
                ", startAddress=" + startAddress +
                ", distance='" + distance + '\'' +
                ", duraton=" + duration +
                '}';
    }
}
