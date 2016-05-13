<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>permis</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Permis</sld:Title>
      <FeatureTypeStyle>
        
        <!-- AVIS ATTENTE -->
        <Rule>
           <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <sld:Name>ATTENTE</sld:Name>
          <sld:Title>En attente</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>6</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#1338c7</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
        
        <!-- AVIS FAVORABLE -->
        <Rule>
         
          <sld:Name>FAVORABLE</sld:Name>
          <sld:Title>Favorable</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>1</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#c058e07</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
        
        <!-- AVIS DEFAVORABLE -->
        <Rule>
          <sld:Name>DEFAVORABLE</sld:Name>
          <sld:Title>Défavorable</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>2</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#c4050a</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
        
        <!-- AVIS RM -->
        <Rule>
          <sld:Name>RFF</sld:Name>
          <sld:Title>RFF</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>5</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#FF6400</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
        
        <!-- AVIS RFF -->
        <Rule>
          <sld:Name>RM</sld:Name>
          <sld:Title>RM</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>3</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#FFAA00</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
        
          <!-- AVIS SRFF -->
        <Rule>
          <sld:Name>SRFF</sld:Name>
          <sld:Title>SRFF</sld:Title>
            <ogc:Filter>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>avis</ogc:PropertyName>
                <ogc:Literal>4</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Filter>
            <PointSymbolizer>
              <Graphic>
                <Mark>
                  <WellKnownName>star</WellKnownName>
                  <Fill>
                    <CssParameter name="fill">#FFFF00</CssParameter>
                    <CssParameter name="fill-opacity">1</CssParameter>
                  </Fill>
                  <Stroke>
                    <CssParameter name="stroke">#424041</CssParameter>
                    <CssParameter name="stroke-width">1</CssParameter>
                  </Stroke>
                </Mark>
                <Size>16</Size>
              </Graphic>
            </PointSymbolizer>
          </Rule>
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>