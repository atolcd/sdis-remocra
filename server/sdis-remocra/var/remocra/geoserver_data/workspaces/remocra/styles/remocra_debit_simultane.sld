<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Débit Simultané</sld:Name>
    <sld:UserStyle>
      <sld:Name>Débit Simultané</sld:Name>
      <sld:Title>Débit Simultané</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>R</sld:Name>
          <sld:Title>R</sld:Title>

          <TextSymbolizer>
            <Geometry>
               <ogc:PropertyName>geom_ds</ogc:PropertyName>
            </Geometry>

             <Label>
                <ogc:PropertyName>debit_retenu</ogc:PropertyName>
             </Label>

             <Font>
                <CssParameter name="font-family">SansSerif</CssParameter>
                <CssParameter name="font-size">12</CssParameter>
                <CssParameter name="font-style">normal</CssParameter>
                <CssParameter name="font-weight">normal</CssParameter>
             </Font>

             <LabelPlacement>
                <PointPlacement>
                   <AnchorPoint>
                      <AnchorPointX>0.5</AnchorPointX>
                      <AnchorPointY>0</AnchorPointY>
                   </AnchorPoint>
                   <Displacement>
                      <DisplacementX>0</DisplacementX>
                      <DisplacementY>10</DisplacementY>
                   </Displacement>
                </PointPlacement>
             </LabelPlacement>

             <Fill>
                <CssParameter name="fill">#000000</CssParameter>
             </Fill>

             <Graphic>
                <Mark>
                  <WellKnownName>Square</WellKnownName>
                  <Fill>
                     <CssParameter name="fill">#FFFFFF</CssParameter>
                     <CssParameter name="fill-opacity">0.7</CssParameter>
                  </Fill>
                </Mark>
             </Graphic>

             <VendorOption name="graphic-resize">stretch</VendorOption>
             <VendorOption name="graphic-margin">2</VendorOption>
             <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>

          <LineSymbolizer>
            <Geometry>
               <ogc:PropertyName>geom_h</ogc:PropertyName>
            </Geometry>
             <Stroke>
               <CssParameter name="stroke">#FFFF00</CssParameter>
               <CssParameter name="stroke-width">3</CssParameter>
               <CssParameter name="stroke-linecap">round</CssParameter>
               <CssParameter name="stroke-dasharray">1 10</CssParameter>
             </Stroke>
           </LineSymbolizer>
          
          <sld:PointSymbolizer>
             <sld:Graphic>
                <sld:ExternalGraphic>
                   <OnlineResource xlink:type="simple" xlink:href="eau/debits_simultanes/${image}" />
                   <sld:Format>image/png</sld:Format>
                </sld:ExternalGraphic>
                <sld:Size>20</sld:Size>
             </sld:Graphic>
          </sld:PointSymbolizer>
          
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>