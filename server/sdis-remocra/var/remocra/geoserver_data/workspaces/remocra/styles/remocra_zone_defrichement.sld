<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>zone_defrichement</sld:Name>
    <sld:UserStyle>
      <sld:Name>Zone_defrichement</sld:Name>
      <sld:Title>Zone_defrichement</sld:Title>

      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>

          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FFFF5F</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>