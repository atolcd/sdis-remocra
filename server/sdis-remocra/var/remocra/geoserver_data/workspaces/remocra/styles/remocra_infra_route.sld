<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>infra_route</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Infrastructure routière</sld:Title>
      <FeatureTypeStyle>  

        <!-- Station service -->
        <Rule>
          <Name>STATION_SERVICE</Name>
          <Title>Station service</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>STATION SERVICE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="station_service.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- Point kilometrique / point de repère -->
        <Rule>
          <Name>PK_PR</Name>
          <Title>Point kilometrique / point de repère</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>PK PR</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="pk_pr.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- PAU / BAU -->
        <Rule>
          <Name>PAU_BAU</Name>
          <Title>PAU / BAU</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>PAU BAU</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="pau_bau.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- ARRET DE BUS -->
        <Rule>
          <Name>ARRET_BUS</Name>
          <Title>ARRET DE BUS</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>  
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>ARRET_BUS</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="habillage_urbain/arret_bus.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
              <sld:Size>17</sld:Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- Etiquettes des stations services -->
      <!-- Niveaux 20,19-->
      <FeatureTypeStyle>
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>STATION SERVICE</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>nom</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Arial</CssParameter>
              <CssParameter name="font-size">7</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>0</DisplacementX>
                  <DisplacementY>16</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>

            <VendorOption name="autoWrap">50</VendorOption>
            <VendorOption name="maxDisplacement">10</VendorOption>
            <VendorOption name="spaceAround">1</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- Etiquettes des pk pr -->
      <!-- Niveaux 20,19-->
      <FeatureTypeStyle>
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>sous_type</ogc:PropertyName>
              <ogc:Literal>PK PR</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>commentaire</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Arial</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">bold</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>-1</DisplacementX>
                  <DisplacementY>-5</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
            </Fill>
            <VendorOption name="autoWrap">50</VendorOption>
            <VendorOption name="maxDisplacement">10</VendorOption>
            <VendorOption name="spaceAround">1</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>