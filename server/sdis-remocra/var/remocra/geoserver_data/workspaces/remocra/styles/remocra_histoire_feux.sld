<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>histoire_feux</sld:Name>
    <sld:UserStyle>
      <sld:Name>Histoire des feux</sld:Name>
      <sld:Title>Histoire des feux</sld:Title>

      <!-- Avant 1960 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 1960</sld:Name>
          <sld:Title>Avant 1960</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsLessThan>
              <ogc:PropertyName>annee</ogc:PropertyName>
              <ogc:Literal>1960</ogc:Literal>
            </ogc:PropertyIsLessThan>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FFFF00</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Avant 1970 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 1970</sld:Name>
          <sld:Title>Avant 1970</sld:Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1960</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1970</ogc:Literal>
              </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF9966</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Avant 1980 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 1980</sld:Name>
          <sld:Title>Avant 1980</sld:Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1970</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1980</ogc:Literal>
              </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF6633</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Avant 1990 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 1990</sld:Name>
          <sld:Title>Avant 1990</sld:Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1980</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1990</ogc:Literal>
              </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF3333</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Avant 2000 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 2000</sld:Name>
          <sld:Title>Avant 2000</sld:Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>1990</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>2000</ogc:Literal>
              </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Avant 2010 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Avant 2010</sld:Name>
          <sld:Title>Avant 2010</sld:Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>2000</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                <ogc:PropertyName>annee</ogc:PropertyName>
                <ogc:Literal>2010</ogc:Literal>
              </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#C5000B</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Après 2010 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Après 2010</sld:Name>
          <sld:Title>Après 2010</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyName>annee</ogc:PropertyName>
              <ogc:Literal>2010</ogc:Literal>
            </ogc:PropertyIsGreaterThanOrEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#000000</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>