<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>remocra_residence_perimetre</sld:Name>
    <sld:UserStyle>
      <sld:Name>remocra_residence_perimetre</sld:Name>
      <sld:Title>Zone de résidence</sld:Title>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>
        <sld:Rule>
           <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>20000</sld:MaxScaleDenominator>
          <sld:Name>residence_perimetre</sld:Name>
          <sld:Title>Zone de résidence</sld:Title>
          <sld:PolygonSymbolizer>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#000000</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
        <!-- Etiquettes des Perimetre residences-->
       <sld:FeatureTypeStyle>
        <sld:Rule>
         <sld:MinScaleDenominator>0</sld:MinScaleDenominator>
          <sld:MaxScaleDenominator>5000</sld:MaxScaleDenominator>
          <sld:TextSymbolizer>
            <sld:Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </sld:Label>
            <sld:Font>
              <sld:CssParameter name="font-family">Arial</sld:CssParameter>
              <sld:CssParameter name="font-family">Sans-Serif</sld:CssParameter>
              <sld:CssParameter name="font-size">10</sld:CssParameter>
              <sld:CssParameter name="font-style">bold</sld:CssParameter>
            </sld:Font>
            <sld:LabelPlacement>
              <sld:PointPlacement>
                <sld:AnchorPoint>
                  <sld:AnchorPointX>0.5</sld:AnchorPointX>
                  <sld:AnchorPointY>0.5</sld:AnchorPointY>
                </sld:AnchorPoint>
                <sld:Displacement>
                  <sld:DisplacementX>0</sld:DisplacementX>
                  <sld:DisplacementY>-20</sld:DisplacementY>
                </sld:Displacement>
                  </sld:PointPlacement>
            </sld:LabelPlacement>
             <Halo>
              <Radius>1</Radius>
            </Halo>
            <sld:VendorOption name="autoWrap">50</sld:VendorOption>
            <sld:VendorOption name="maxDisplacement">10</sld:VendorOption>
            <sld:VendorOption name="spaceAround">2</sld:VendorOption>
            <sld:VendorOption name="conflictResolution">false</sld:VendorOption>
          </sld:TextSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>     
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>