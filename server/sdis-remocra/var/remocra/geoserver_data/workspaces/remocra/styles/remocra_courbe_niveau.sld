<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" 
 xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd" 
 xmlns="http://www.opengis.net/sld" 
 xmlns:ogc="http://www.opengis.net/ogc" 
 xmlns:xlink="http://www.w3.org/1999/xlink" 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <!-- a Named Layer is the basic building block of an SLD document -->
  <NamedLayer>
    <Name>Courbe niveau</Name>
    <UserStyle>
      <Title>Courbe niveau</Title>
      <Abstract>A sample style that draws a line</Abstract>
      <FeatureTypeStyle>
        <Rule>
          <Name>Courbe niveau</Name>
          <Title>Courbe niveau</Title>
          <Abstract>Courbe niveau</Abstract>
          
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          
          <LineSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
               <CssParameter name="stroke-opacity">0.25</CssParameter>
            </Stroke>
          </LineSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>