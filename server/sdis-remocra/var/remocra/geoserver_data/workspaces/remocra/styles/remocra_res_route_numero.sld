<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>res_route_numero</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Numérotation des rues</sld:Title>
      
      <!-- NUMEROTATION DES VOIES -->
      <FeatureTypeStyle>
        
        <!-- Niveau 20 et 19 -->
        <!-- Numéros de debut de rue, à droite -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_debut</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_debut_droit</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-15</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de fin de rue, à droite -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_fin</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_fin_droit</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-15</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de debut de rue, à gauche -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_debut</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_debut_gauche</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>15</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de fin de rue, à gauche -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_fin</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_fin_gauche</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>15</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        
        <!-- Niveau 18 -->
        <!-- Numéros de debut de rue, à droite -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_debut</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_debut_droit</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">6</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>10</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de fin de rue, à droite -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_fin</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_fin_droit</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">6</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>10</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de debut de rue, à gauche -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_debut</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_debut_gauche</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">6</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-10</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        <!-- Numéros de fin de rue, à gauche -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <TextSymbolizer>
            <Geometry>
              <ogc:PropertyName>geometrie_fin</ogc:PropertyName>
            </Geometry>
            <Label>
              <ogc:PropertyName>adresse_fin_gauche</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">6</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-10</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#0000FF</CssParameter>
            </Fill>
            <VendorOption name="followLine">false</VendorOption>
            <VendorOption name="forceLeftToRight">false</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>