<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Aire de retournement</sld:Name>
    <sld:UserStyle>
      <sld:Name>Aire de retournement</sld:Name>
      <sld:Title>Aire de retournement</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>airedere</sld:Name>
          <sld:Title>airedere</sld:Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>9000</MaxScaleDenominator>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="dfci_aire_retournement.png"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>17</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <sld:Name>airedere</sld:Name>
          <sld:Title>airedere</sld:Title>
          <MinScaleDenominator>9000</MinScaleDenominator>
          <MaxScaleDenominator>25000</MaxScaleDenominator>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="dfci_aire_retournement.png"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>12</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>