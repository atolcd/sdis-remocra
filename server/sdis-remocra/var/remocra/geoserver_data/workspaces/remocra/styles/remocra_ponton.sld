<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>res_route</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Réseau routier</sld:Title>
      
      <FeatureTypeStyle>
        
        <!-- Niveau 20-->
        <Rule>
          <Name>PONTON</Name>
          <Title>Ponton</Title>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>1700</MaxScaleDenominator>
        <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">18</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Niveau 19 -->        
        <Rule>
          <Name>PONTON</Name>
          <Title>Ponton</Title>
          <MinScaleDenominator>1700</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">14</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Niveau 18 -->
        <Rule>
          <Name>PONT</Name>
          <Title>Pont</Title>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">10</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Niveaux intermédiaires : pas de classification sur ouvrage ou sur gabarit -->
        <Rule>
          <MinScaleDenominator>6800</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <LineSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">8</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Autres niveaux : pas de classification sur ouvrage ou sur gabarit -->
        <Rule>
          <MinScaleDenominator>100000</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
      <!-- INTERIEUR DE PONTON -->
      <FeatureTypeStyle>
        
        <!-- Niveau 20 -->
        <Rule>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>1700</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">16</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Niveau 19 -->
        <Rule>
          <MinScaleDenominator>1700</MinScaleDenominator>
          <MaxScaleDenominator>3400</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">12</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
        
        <!-- Niveau 18 -->
        <Rule>
          <MinScaleDenominator>3400</MinScaleDenominator>
          <MaxScaleDenominator>6800</MaxScaleDenominator>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">8</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>

        <!-- Niveaux intermédiaires -->
        <Rule>
          <MinScaleDenominator>6800</MinScaleDenominator>
          <MaxScaleDenominator>100000</MaxScaleDenominator>
          <LineSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            <Stroke>
              <CssParameter name="stroke">#DDDDDD</CssParameter>
              <CssParameter name="stroke-width">6</CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
              <CssParameter name="stroke-linejoin">round</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
      </FeatureTypeStyle>
      
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>