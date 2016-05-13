<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>point_cote</sld:Name>
    <sld:UserStyle>
      <sld:Name>Point coté</sld:Name>
      <sld:Title>Point coté</sld:Title>

      <!-- NORMAL -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>

          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>

          <!-- Etiquette -->
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>altitude</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <Displacement>
                  <DisplacementX>0</DisplacementX>
                  <DisplacementY>2</DisplacementY>
                </Displacement>
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

          <!-- Point -->
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#000000</CssParameter>
                </Fill>
              </Mark>
              <Size>2</Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>