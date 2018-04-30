<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" 
xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd" 
xmlns="http://www.opengis.net/sld" 
xmlns:ogc="http://www.opengis.net/ogc" 
xmlns:xlink="http://www.w3.org/1999/xlink" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<NamedLayer>
<Name>selection</Name>
<UserStyle>
<Title>Sélection courante</Title>
<FeatureTypeStyle>
<Rule>
<Title>Entité surfacique</Title>
<ogc:Filter>
<ogc:PropertyIsEqualTo>
<ogc:Function name="dimension">
<ogc:PropertyName>geometrie</ogc:PropertyName>
</ogc:Function>
<ogc:Literal>2</ogc:Literal>
</ogc:PropertyIsEqualTo>
</ogc:Filter>
<PolygonSymbolizer>
<Fill>
<CssParameter name="fill">#ffff00</CssParameter>
  <CssParameter name="fill-opacity">0.5</CssParameter>
</Fill>
<Stroke>
<CssParameter name="stroke">#000000</CssParameter>
<CssParameter name="stroke-width">0.5</CssParameter>
</Stroke>
</PolygonSymbolizer>
</Rule>
<Rule>
<Title>Entité linéaire</Title>
<ogc:Filter>
<ogc:PropertyIsEqualTo>
<ogc:Function name="dimension">
<ogc:PropertyName>geometrie</ogc:PropertyName>
</ogc:Function>
<ogc:Literal>1</ogc:Literal>
</ogc:PropertyIsEqualTo>
</ogc:Filter>
<LineSymbolizer>
<Stroke>
<CssParameter name="stroke">#000000</CssParameter>
<CssParameter name="stroke-opacity">1</CssParameter>
<CssParameter name="stroke-width">4</CssParameter>
</Stroke>
</LineSymbolizer>
<LineSymbolizer>
<Stroke>
<CssParameter name="stroke">#ffff00</CssParameter>
<CssParameter name="stroke-opacity">1</CssParameter>
<CssParameter name="stroke-width">2</CssParameter>
</Stroke>
</LineSymbolizer>
</Rule>
<Rule>
<Title>Entité ponctuelle</Title>
<ElseFilter/>
<PointSymbolizer>
<Graphic>
<Mark>
<WellKnownName>circle</WellKnownName>
<Fill>
<CssParameter name="fill">#ffff00</CssParameter>
</Fill>
<Stroke>
<CssParameter name="stroke">#000000</CssParameter>
<CssParameter name="stroke-width">1</CssParameter>
</Stroke>
</Mark>
<Size>12</Size>
</Graphic>
</PointSymbolizer>
</Rule>
</FeatureTypeStyle>
</UserStyle>
</NamedLayer>
</StyledLayerDescriptor>