<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>iaires</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Aires dfci</sld:Title>
      <FeatureTypeStyle>  

        <!-- aire de croisement -->
        <Rule>
          <Name>CROISEMENT</Name>
          <Title>aire de croisement</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>type_aire</ogc:PropertyName>
              <ogc:Literal>AIRE DE CROISEMENT</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="dfci_aire_croisement.png"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>17</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </Rule>
          <!-- aire de retournement -->
        <Rule>
          <Name>RETOURNEMENT</Name>
          <Title>aire de retournement</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>type_aire</ogc:PropertyName>
              <ogc:Literal>AIRE DE RETOURNEMENT</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="dfci_aire_retournement.png"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>17</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>