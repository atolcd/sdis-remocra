<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>pt_eau</sld:Name>
    <sld:UserStyle>
      <sld:Name>normal</sld:Name>
      <sld:Title>Point d'eau</sld:Title>
      <FeatureTypeStyle>
        <!-- Citerne DFCI HBE -->
        <Rule>
          <Name>CITERNE_DFCI_HBE</Name>
          <Title>Citerne DFCI HBE</Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>type_hydrant</ogc:PropertyName>
                <ogc:Literal>CITERNE DFCI</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>trappe_hbe</ogc:PropertyName>
                <ogc:Literal>OUI</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>utilisable_hbe</ogc:PropertyName>
                <ogc:Literal>OUI</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:And>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="citerne_dfci_hbe.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- Citerne DFCI -->
        <Rule>
          <Name>CITERNE_DFCI</Name>
          <Title>Citerne DFCI</Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>type_hydrant</ogc:PropertyName>
                <ogc:Literal>CITERNE DFCI</ogc:Literal>
              </ogc:PropertyIsEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>utilisable_hbe</ogc:PropertyName>
                <ogc:Literal>NON</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:And>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="citerne_dfci.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- Autre point d'eau HBE -->
        <Rule>
          <Name>AUTRE_POINT_EAU_HBE</Name>
          <Title>Autre point d'eau HBE</Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsNotEqualTo>
                <ogc:PropertyName>type_hydrant</ogc:PropertyName>
                <ogc:Literal>CITERNE DFCI</ogc:Literal>
              </ogc:PropertyIsNotEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>utilisable_hbe</ogc:PropertyName>
                <ogc:Literal>OUI</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:And>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="autre_point_eau_hbe.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        <!-- Autre point d'eau -->
        <Rule>
          <Name>AUTRE_POINT_EAU</Name>
          <Title>Autre point d'eau</Title>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsNotEqualTo>
                <ogc:PropertyName>type_hydrant</ogc:PropertyName>
                <ogc:Literal>CITERNE DFCI</ogc:Literal>
              </ogc:PropertyIsNotEqualTo>
              <ogc:PropertyIsEqualTo>
                <ogc:PropertyName>utilisable_hbe</ogc:PropertyName>
                <ogc:Literal>NON</ogc:Literal>
              </ogc:PropertyIsEqualTo>
            </ogc:And>
          </ogc:Filter>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource xlink:type="simple" xlink:href="autre_pt_eau.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
            </Graphic>
          </PointSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>