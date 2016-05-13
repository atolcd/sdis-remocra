<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>ets_surf</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Etablissement surfacique</sld:Title>
      <FeatureTypeStyle>
        
        <!-- EMPRISE ERP -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>EMPRISE_ERP</Name>
          <Title>ERP (emprise)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>EMPRISE ERP</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#ff0000</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ENTREPRISE -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ENTREPRISE</Name>
          <Title>Entreprise</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ENTREPRISE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#cccccc</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- EMPRISE SERVICE PUBLIC -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>EMPRISE_SERVICE_PUBLIC</Name>
          <Title>Service public (emprise)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>EMPRISE SERVICE PUBLIC</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#ff950e</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
        
           
             <!-- ZONE INTERET PARC-->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>SQUARE</Name>
          <Title>SQUARE</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>SQUARE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#CCFFCC</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- EMPRISE SPORT PLEIN AIR -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>EMPRISE_SPORT_PLEIN_AIR</Name>
          <Title>Sport de plein air (emprise)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>EMPRISE SPORT PLEIN AIR</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#579d1c</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZAC -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZAC</Name>
          <Title>ZAC</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZAC</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#666666</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZONE INTERET CAMPING -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_CAMPING</Name>
          <Title>Camping (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET CAMPING</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#ff0000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZONE INTERET ERP ICPE -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_ERP_ICPE</Name>
          <Title>ERP ICPE (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET ERP ICPE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#ff0000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <!-- ZONE INTERET ENTREPRISE -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_ENTREPRISE</Name>
          <Title>Entreprise (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET ENTREPRISE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#cccccc</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZONE INTERET ETARE -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_ETARE</Name>
          <Title>ETARE (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET ETARE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#b80047</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZONE INTERET SERVICE PUBLIC -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_SERVICE_PUBLIC</Name>
          <Title>Service public (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET SERVICE PUBLIC</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#ff950e</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
        
        <!-- ZONE INTERET SPORT PLEIN AIR -->
        <Rule>
          <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <Name>ZONE_INTERET_SPORT_PLEIN_AIR</Name>
          <Title>Sport de plein air (zone d'intérêt)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ZONE INTERET SPORT PLEIN AIR</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#579d1c</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>