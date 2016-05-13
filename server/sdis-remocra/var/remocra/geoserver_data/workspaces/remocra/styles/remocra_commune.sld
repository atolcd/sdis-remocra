<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>commune</sld:Name>
    <sld:UserStyle>
      <sld:Name>remocra_commune</sld:Name>
      <sld:Title>Limite de commune</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>commune</sld:Name>
          <sld:Title>Limite de commune</sld:Title>
          <sld:PolygonSymbolizer>
            <sld:Fill>
                <sld:CssParameter name="fill">#000000</sld:CssParameter>
                <sld:CssParameter name="fill-opacity">0</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#FF33CC</sld:CssParameter>
              <sld:CssParameter name="stroke-width">1</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>