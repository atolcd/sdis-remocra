<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>piste</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Piste</sld:Title>
      
      <!-- PISTE -->
      <FeatureTypeStyle>
        
        <!-- Niveau 20-->
        <!-- support des pistes -->
        <Rule>
          <sld:Name>support</sld:Name>
          <sld:Title>support des pistes</sld:Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>5000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          </Rule>
        <!-- Classement 1 -->
        <Rule>
          <sld:Name>premiere_categorie</sld:Name>
          <sld:Title>Première catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>5000</MaxScaleDenominator>
          <!--Exterieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">8</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          <!--Intérieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FFFFFF</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 2 -->
        <Rule>
          <sld:Name>seconde_categorie</sld:Name>
          <sld:Title>Seconde catégorie</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>2</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>3</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>5000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 3 -->
        <Rule>
          <sld:Name>troisieme_categorie</sld:Name>
          <sld:Title>Troisième catégorie (groupe d'intervantion)</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>4</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>5000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-dasharray">30 15</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 4 -->
        <Rule>
          <sld:Name>quatrieme_categorie</sld:Name>
          <sld:Title>Quatrième catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>5</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>5000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-dasharray">8 6</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>

        <!-- Niveau 19 -->
        <!-- support des pistes -->
        <Rule>
          <sld:Name>support</sld:Name>
          <sld:Title>support des pistes</sld:Title>
          <MinScaleDenominator>5000</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          </Rule>
        <!-- Classement 1 -->
        <Rule>
          <sld:Name>premiere_categorie</sld:Name>
          <sld:Title>Première catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>5000</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>
          <!--Exterieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">5</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          <!--Interieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FFFFFF</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 2 -->
        <Rule>
          <sld:Name>seconde_categorie</sld:Name>
          <sld:Title>Seconde catégorie</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>2</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>3</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>
          <MinScaleDenominator>5000</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 3 -->
        <Rule>
          <sld:Name>troisieme_categorie</sld:Name>
          <sld:Title>Troisième catégorie (groupe d'intervantion)</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>4</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>5000</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
              <CssParameter name="stroke-dasharray">30 15</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 4 -->
        <Rule>
          <sld:Name>quatrieme_categorie</sld:Name>
          <sld:Title>Quatrième catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>5</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>5000</MinScaleDenominator>
          <MaxScaleDenominator>10000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">2</CssParameter>
              <CssParameter name="stroke-dasharray">8 6</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>

        <!-- Niveau 18 -->
        <!-- support des pistes -->
        <Rule>
          <sld:Name>support</sld:Name>
          <sld:Title>support des pistes</sld:Title>
          <MinScaleDenominator>10000</MinScaleDenominator>
          <MaxScaleDenominator>30000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          </Rule>
        <!-- Classement 1 -->
        <Rule>
          <sld:Name>premiere_categorie</sld:Name>
          <sld:Title>Première catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>10000</MinScaleDenominator>
          <MaxScaleDenominator>30000</MaxScaleDenominator>
          <!--Exterieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">5</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
          <!--Interieur-->
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FFFFFF</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 2 -->
        <Rule>
          <sld:Name>seconde_categorie</sld:Name>
          <sld:Title>Seconde catégorie</sld:Title>
          <ogc:Filter>
            <ogc:Or>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>2</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>classement</ogc:PropertyName>
                <ogc:Literal>3</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:Or>
          </ogc:Filter>
          <MinScaleDenominator>10000</MinScaleDenominator>
          <MaxScaleDenominator>30000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 3 -->
        <Rule>
          <sld:Name>troisieme_categorie</sld:Name>
          <sld:Title>Troisième catégorie (groupe d'intervantion)</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>4</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>10000</MinScaleDenominator>
          <MaxScaleDenominator>30000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-dasharray">30 15</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Classement 4 -->
        <Rule>
          <sld:Name>quatrieme_categorie</sld:Name>
          <sld:Title>Quatrième catégorie</sld:Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>classement</ogc:PropertyName>
              <ogc:Literal>5</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <MinScaleDenominator>10000</MinScaleDenominator>
          <MaxScaleDenominator>30000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-dasharray">8 6</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>

        <!-- Niveaux intermédiaires : pas de classification sur classement -->
        <Rule>
          <MinScaleDenominator>30000</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <LineSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            <Stroke>
              <CssParameter name="stroke">#FF0000</CssParameter>
              <CssParameter name="stroke-width">4</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- NOM DES PISTES -->
      <FeatureTypeStyle>
        
        <!-- Niveau 20 -->
        <!-- Nom des pistes -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>1700</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>com_nme</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">12</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>0</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>3</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
            </Fill>
            <VendorOption name="repeat">300</VendorOption>
            <VendorOption name="followLine">true</VendorOption>
            <VendorOption name="maxAngleDelta">45</VendorOption>
            <VendorOption name="maxDisplacement">50</VendorOption>
            <VendorOption name="group">true</VendorOption>
            <VendorOption name="conflictResolution">true</VendorOption>
            <VendorOption name="labelAllGroup">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        
        <!-- Niveau 19 -->       
        <!-- Nom des piste -->
        <Rule>
          <MinScaleDenominator>1700</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>com_nme</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">10</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-2</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>2</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
            </Fill>
            <VendorOption name="repeat">300</VendorOption>
            <VendorOption name="followLine">true</VendorOption>
            <VendorOption name="maxAngleDelta">45</VendorOption>
            <VendorOption name="maxDisplacement">50</VendorOption>
            <VendorOption name="group">true</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
            <VendorOption name="labelAllGroup">false</VendorOption>
          </TextSymbolizer>
        </Rule>
        
        <!-- Niveau 18 -->
        <!-- Nom des piste -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>com_nme</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-2</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>2</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
            </Fill>
            <VendorOption name="repeat">300</VendorOption>
            <VendorOption name="followLine">true</VendorOption>
            <VendorOption name="maxAngleDelta">45</VendorOption>
            <VendorOption name="maxDisplacement">50</VendorOption>
            <VendorOption name="group">true</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
            <VendorOption name="labelAllGroup">false</VendorOption>
          </TextSymbolizer>
        </Rule>      
      
        <!-- Niveau 17 -->
        <!-- Nom des piste -->
        <Rule>
          <MinScaleDenominator>6800</MinScaleDenominator>
          <MaxScaleDenominator>9000</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>com_nme</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">SansSerif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <LinePlacement>
                <PerpendicularOffset>-2</PerpendicularOffset>
              </LinePlacement>
            </LabelPlacement>
            <Halo>
              <Radius>2</Radius>
            </Halo>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
            </Fill>
            <VendorOption name="repeat">300</VendorOption>
            <VendorOption name="followLine">true</VendorOption>
            <VendorOption name="maxAngleDelta">45</VendorOption>
            <VendorOption name="maxDisplacement">50</VendorOption>
            <VendorOption name="group">true</VendorOption>
            <VendorOption name="conflictResolution">false</VendorOption>
            <VendorOption name="labelAllGroup">false</VendorOption>
          </TextSymbolizer>
        </Rule>      

      </FeatureTypeStyle>
      
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>