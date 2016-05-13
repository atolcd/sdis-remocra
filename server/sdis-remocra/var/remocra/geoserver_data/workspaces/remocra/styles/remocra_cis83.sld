<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>cis83</sld:Name>
    <sld:UserStyle>
      <sld:Name>remocra_cis83</sld:Name>
      <sld:Title>Centre d'incendie</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>cis83</sld:Name>
          <sld:Title>Centre d'incendie</sld:Title>
          
          <!-- On ne prend pas les CIS projets -->
          <ogc:Filter>
            <ogc:Not>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>com_nme</ogc:PropertyName>
                <ogc:Literal></ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Not>
          </ogc:Filter>
          
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="remocra_cis83.png"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>17</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>