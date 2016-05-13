<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Hydrant PIBI / PENA</sld:Name>
    <sld:UserStyle>
      <sld:Name>Hydrant PIBI / PENA</sld:Name>
      <sld:Title>Hydrant PIBI / PENA</sld:Title>


      <!-- Niveau 20 à 17 -->
      <FeatureTypeStyle>
        <Rule>

          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>13600</MaxScaleDenominator>
          <!-- Le nom de l'image est calculé dans la requête -->
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="eau/${img}"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>${height}</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>

          <!-- Etiquettes des hydrants -->
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>numero</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">11</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.0</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>0</DisplacementX>
                  <DisplacementY>-25</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1.5</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0f0fff</CssParameter>
            </Fill>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
          
        </Rule>
      </FeatureTypeStyle> 
           
      <!-- Niveau 15 à 17 -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>
          <MinScaleDenominator>13600</MinScaleDenominator>
          <MaxScaleDenominator>35000</MaxScaleDenominator>
          <!-- Le nom de l'image est calculé dans la requête -->
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="eau/${img}"/>
                <sld:Format>image/png</sld:Format>
              </sld:ExternalGraphic>
              <sld:Size>8</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
          
        </Rule>
      </FeatureTypeStyle> 
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>