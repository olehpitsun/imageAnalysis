package sample.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "area")
public class RegionDetectByColorListWrapper {

    private List<RegionDetectByColor> regionDetectByColors;

    @XmlElement(name = "area")
    public List<RegionDetectByColor> getAreaList() {
        return regionDetectByColors;
    }

    public void setAreaList(List<RegionDetectByColor> regionDetectByColors) {
        this.regionDetectByColors = regionDetectByColors;
    }
}