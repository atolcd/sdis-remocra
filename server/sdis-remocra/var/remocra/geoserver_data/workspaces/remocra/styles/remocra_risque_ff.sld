<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>risque_ff</sld:Name>
    <sld:UserStyle>
      <sld:Name>Risques feux de forêt</sld:Name>
      <sld:Title>Risques feux de forêt</sld:Title>
      
      <!-- R -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
              <ogc:Literal>R</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- B0 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>BO</sld:Name>
          <sld:Title>BO</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsLike wildCard="%" singleChar="." escape="\">
              <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
              <ogc:Literal>%BO%</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF00FF</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- B B1 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>B B1</sld:Name>
          <sld:Title>B B1</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
                <ogc:Literal>B</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
                <ogc:Literal>B1</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- B2 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>B2</sld:Name>
          <sld:Title>B2</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
              <ogc:Literal>B2</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0084D1</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- B3 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>B3</sld:Name>
          <sld:Title>B3</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
              <ogc:Literal>B3</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#00B8FF</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- NCR -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>NCR</sld:Name>
          <sld:Title>NCR</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>zonage_pprif</ogc:PropertyName>
              <ogc:Literal>NCR</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>50000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FFFFFF</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>