<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>service_public</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Service public</sld:Title>
      
      <!-- Pictogrammes des services -->
      <FeatureTypeStyle>
        <!-- Niveaux 20 et 19 -->
        <!-- Pictogramme -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>square</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#FF6600</CssParameter>
                  <CssParameter name="fill-opacity">0.25</CssParameter>
                </Fill>
                <Stroke>
                  <CssParameter name="stroke">#FF6600</CssParameter>
                  <CssParameter name="stroke-width">1</CssParameter>
                </Stroke>
               
              </Mark>
              <Size>12</Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        
        <!-- Niveaux 18 -->
        <!-- Pictogramme -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>square</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#FF6600</CssParameter>
                  <CssParameter name="fill-opacity">0.25</CssParameter>
                </Fill>
                <Stroke>
                  <CssParameter name="stroke">#FF6600</CssParameter>
                  <CssParameter name="stroke-width">1</CssParameter>
                </Stroke>
              </Mark>
              <Size>6</Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- Etiquettes des services -->
      <!-- Niveaux 20,19-->
      <FeatureTypeStyle>
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">7</CssParameter>
              <CssParameter name="font-style">italic</CssParameter>
                    
            </Font>
             <Fill>
              <CssParameter name="fill">#FF6600</CssParameter>
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
              <Radius>1</Radius>
            </Halo>
              
            <Fill>
              <CssParameter name="fill">#FF3300</CssParameter>
            </Fill>
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