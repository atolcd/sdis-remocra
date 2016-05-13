<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>hydrant</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>PIBI dans une tournee</sld:Title>
      
      <!-- Pictogrammes des établissements -->
      <FeatureTypeStyle>

          <!-- hydrant affecté -->
        <Rule>
          <Name>hydrant affectés à une tournée</Name>
          <Title>hydrant affectés à une tournée</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>  
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#33FF66</CssParameter>
                  <CssParameter name="fill-opacity">1</CssParameter>
                </Fill>
                <Stroke>
                  <CssParameter name="stroke">#FFFFFF</CssParameter>
                  <CssParameter name="stroke-width">0.5</CssParameter>
                </Stroke>
              </Mark>
              <Size>12</Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>        

     
      <!-- Etiquettes des numéros de tournée -->
      <!-- Niveaux 20,19-->
      <FeatureTypeStyle>
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>tournee</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Tahoma</CssParameter>
              <CssParameter name="font-size">7</CssParameter>
              <CssParameter name="font-style">italic</CssParameter>
              <CssParameter name="font-weight">bold</CssParameter>
            </Font>
                <Fill>
                  <CssParameter name="fill">#AA0000</CssParameter>
                </Fill>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>0</DisplacementX>
                  <DisplacementY>-20</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1.5</Radius>
            </Halo>
            <VendorOption name="autoWrap">50</VendorOption>
            <VendorOption name="maxDisplacement">10</VendorOption>
            <VendorOption name="spaceAround">2</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
        
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>