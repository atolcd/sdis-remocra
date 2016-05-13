<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>permis_zonage</sld:Name>
    <sld:UserStyle>
      <sld:Name>Zonage des permis</sld:Name>
      <sld:Title>Zonage des permis</sld:Title>

      <!-- NORMAL -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Normal</sld:Name>
          <sld:Title>Normal</sld:Title>
            <ogc:Filter>
            <ogc:PropertyIsLike wildCard="%" singleChar="." escape="\">
              <ogc:PropertyName>precision_adresse</ogc:PropertyName>
              <ogc:Literal>%NORMAL%</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0047FF</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- IMPORTANT -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Important</sld:Name>
          <sld:Title>Important</sld:Title>
            <ogc:Filter>
            <ogc:PropertyIsLike wildCard="%" singleChar="." escape="\">
              <ogc:PropertyName>precision_adresse</ogc:PropertyName>
              <ogc:Literal>%IMPORTANT%</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF950E</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

      <!-- MAJEUR -->
      <FeatureTypeStyle>
        <Rule>
          <sld:Name>Majeur</sld:Name>
          <sld:Title>Majeur</sld:Title>
            <ogc:Filter>
            <ogc:PropertyIsLike wildCard="%" singleChar="." escape="\">
              <ogc:PropertyName>precision_adresse</ogc:PropertyName>
              <ogc:Literal>%MAJEUR%</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>
          <MinScaleDenominator>0</MinScaleDenominator>
          <MaxScaleDenominator>20000</MaxScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FF0000</CssParameter>
              <CssParameter name="fill-opacity">0.25</CssParameter>
            </Fill>
          </PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>