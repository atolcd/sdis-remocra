<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>debrousaillement</sld:Name>
    <sld:UserStyle>
      <sld:Name>debrousaillement</sld:Name>
      <sld:Title>debrousaillement</sld:Title>

      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>

          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator> 
          
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:GraphicFill>
                <sld:Graphic>
                  <sld:Mark>
                    <sld:WellKnownName>shape://slash</sld:WellKnownName>
                     <sld:Stroke>
                        <sld:CssParameter name="stroke">#FFFF62</sld:CssParameter>
                         <sld:CssParameter name="stroke-width">1</sld:CssParameter >
                      </sld:Stroke>
                  </sld:Mark>
                  <sld:Size>8</sld:Size>
                </sld:Graphic>
              </sld:GraphicFill>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#FFFF62</sld:CssParameter >
              <sld:CssParameter name="stroke-width">1</sld:CssParameter >
            </sld:Stroke>
          </sld:PolygonSymbolizer>
          
          
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>