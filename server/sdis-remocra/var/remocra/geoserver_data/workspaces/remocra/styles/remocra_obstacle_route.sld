<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>obstacle_route</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>obstacle</sld:Title>

      <FeatureTypeStyle>
        
      <!-- obstacle infranchissable -->
        <Rule>
          <Name>obstacle_infranchissable</Name>
          <Title>obstacle infranchissable</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>OBSTACLE NON FRANCHISSABLE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="habillage_urbain/obst_inamov.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
              <sld:Size>17</sld:Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      <!-- obstacle amovible -->        
        <Rule>
          <Name>obstacle amovible</Name>
          <Title>obstacle amovible</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>OBSTACLE AMOVIBLE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="habillage_urbain/obst_amov.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
              <sld:Size>17</sld:Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
   
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>