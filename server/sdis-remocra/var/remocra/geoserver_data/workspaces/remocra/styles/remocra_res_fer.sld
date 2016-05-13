<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>res_fer</sld:Name>
    <sld:UserStyle>
      <sld:Name>remocra_res_fer</sld:Name>
      <sld:Title>Réseau ferré</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>res_fer</sld:Name>
          <sld:Title>Réseau ferré</sld:Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>108000</MaxScaleDenominator>
          <sld:LineSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            <sld:Stroke>
              <sld:CssParameter name="stroke">#000000</sld:CssParameter>
              <sld:CssParameter name="stroke-width">5</sld:CssParameter>
            </sld:Stroke>
          </sld:LineSymbolizer>
          <sld:LineSymbolizer>
            <sld:Stroke>
              <sld:GraphicStroke>
                <sld:Graphic>
                  <sld:Mark>
                    <sld:WellKnownName>shape://vertline</sld:WellKnownName>
                    <sld:Stroke>
                      <sld:CssParameter name="stroke">#333333</sld:CssParameter>
                      <sld:CssParameter name="stroke-width">1</sld:CssParameter>
                    </sld:Stroke>
                  </sld:Mark>
                  <sld:Size>12</sld:Size>
                </sld:Graphic>
              </sld:GraphicStroke>
            </sld:Stroke>
          </sld:LineSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>