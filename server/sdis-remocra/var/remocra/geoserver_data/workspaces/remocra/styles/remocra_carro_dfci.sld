<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>carro_dfci</sld:Name>
    <sld:UserStyle>
      <sld:Name>Carroyage DFCI</sld:Name>
      <sld:Title>Carroyage DFCI</sld:Title>

      <!-- CARRES 2x2 KM  & CARRES INTRA 2x2 KM -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>sous_type</ogc:PropertyName>
                <ogc:Literal>CARRES 2x2 KM</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>sous_type</ogc:PropertyName>
                <ogc:Literal>CARRES INTRA 2x2 K</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>

          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>

        </Rule>
      </FeatureTypeStyle>
      
      <!-- CARRES 20x20 KM -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>CARRES 20x20 KM</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>

          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
            </Stroke>
          </PolygonSymbolizer>

        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>