<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>coord_dfci</sld:Name>
    <sld:UserStyle>
      <sld:Name>Coordonnée DFCI</sld:Name>
      <sld:Title>Coordonnée DFCI</sld:Title>

      <!-- NORMAL -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>2x2 KM</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>200000</MaxScaleDenominator>

          <!-- Etiquette -->
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">12</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#000000</CssParameter>
            </Fill>
              <VendorOption name="spaceAround">10</VendorOption>
          </TextSymbolizer>

        </Rule>
      </FeatureTypeStyle>

      <!-- 20x20 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>20x20 KM</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>200000</MinScaleDenominator>
          <MaxScaleDenominator>600000</MaxScaleDenominator>

          <!-- Etiquette -->
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">14</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#000000</CssParameter>
            </Fill>
              <VendorOption name="spaceAround">10</VendorOption>
          </TextSymbolizer>

        </Rule>
      </FeatureTypeStyle>

      <!-- verticales et horizontales -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>sous_type</ogc:PropertyName>
                <ogc:Literal>2x2 VERTICALES</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>sous_type</ogc:PropertyName>
                <ogc:Literal>2x2 HORIZONTALES</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>
          <MinScaleDenominator>200000</MinScaleDenominator>
          <MaxScaleDenominator>600000</MaxScaleDenominator>

          <!-- Etiquette -->
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">14</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#000000</CssParameter>
            </Fill>
              <VendorOption name="spaceAround">10</VendorOption>
          </TextSymbolizer>

        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>