package sample.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "area")
public class RegionDetectByColorListWrapper {

    private List<RegionDetectByColor> regionDetectByColors;

    @XmlElement(name = "area")
    public List<RegionDetectByColor> getArea() {
        return regionDetectByColors;
    }

    public void setArea(List<RegionDetectByColor> regionDetectByColors) {
        this.regionDetectByColors = regionDetectByColors;
    }
}