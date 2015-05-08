package org.example.linking;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.AbstractResourceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.regex.Pattern;


/**
 * Overriding the default {@code ResourceContainer} to prepend the resource's last modification timestamp to the generated path.
 * <p>
 * For example, by default, binary image link is generated like the following:
 * <ul>
 * <li>/site/binaries/content/gallery/myhippoproject/samples/coffee-206142_150.jpg</li>
 * </ul>
 * But, if you configure this {@code ResourceContainer} and {@link #revisionTimestampPrependingEnabled} is true, then you will get the following
 * instead:
 * <ul>
 * <li>/site/binaries/_ht_1384250940000/content/gallery/myhippoproject/samples/coffee-206142_150.jpg</li>
 * </ul>
 * <p>
 * The {@link #revisionTimestampPrependingEnabled} is set in the
 * /site/src/main/resources/META-INF/hst-assembly/overrides/customResourceContainers.xml. That value is set via a system property
 * "revisionTimestampPrependingEnabled" that is set in site.properties
 * </p>
 */
public class RevisionTimePrefixedHippoGalleryImageSetContainer extends AbstractResourceContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevisionTimePrefixedHippoGalleryImageSetContainer.class);

    private static final String REVISION_TIMESTAMP_PREFIX = "/_ht_";
    private static final Pattern REVISION_TIMESTAMP_PATTERN = Pattern.compile("^/_ht_\\d+");

    /**
     * Flag whether or not the revision timestamp prepending should be enabled.
     */
    private boolean revisionTimestampPrependingEnabled;

    /**
     * The nodetype that will be rewritten
     */
    private String nodeType;

    /**
     * Return the flag whether or not the revision timestamp prepending should be enabled.
     *
     * @return the flag whether or not the revision timestamp prepending should be enabled
     */
    public boolean isRevisionTimestampPrependingEnabled() {
        return revisionTimestampPrependingEnabled;
    }

    /**
     * Sets the flag whether or not the revision timestamp prepending should be enabled.
     *
     * @param revisionTimestampPrependingEnabled the flag whether or not the revision timestamp prepending should be enabled
     */
    public void setRevisionTimestampPrependingEnabled(boolean revisionTimestampPrependingEnabled) {
        this.revisionTimestampPrependingEnabled = revisionTimestampPrependingEnabled;
    }

    /**
     * @param nodeType The nodeType for which this resourceContainer is applicable
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return The nodeType for which this resourceContainer is applicable
     */
    @Override
    public String getNodeType() {
        return nodeType;
    }

    /**
     * {@inheritDoc}
     *
     * Prepend the path info by the last modified timestamp (@jcr:lastModified) of the resource node.
     *
     * @param resourceContainerNode resource container node
     * @param resourceNode resource node
     * @param mount mount
     * @return resource node path
     */
    @Override
    public String resolveToPathInfo(Node resourceContainerNode, Node resourceNode, Mount mount) {
        String pathInfo = super.resolveToPathInfo(resourceContainerNode, resourceNode, mount);

        if (isRevisionTimestampPrependingEnabled() && pathInfo != null) {
            try {
                Calendar lastModified = resourceNode.getProperty("jcr:lastModified").getDate();
                pathInfo = new StringBuilder(pathInfo.length() + 20)
                    .append(REVISION_TIMESTAMP_PREFIX)
                    .append(lastModified.getTimeInMillis())
                    .append(pathInfo)
                    .toString();
            } catch (RepositoryException e) {
                LOGGER.warn("RepositoryException while prepending lastModified timestamp.", e);
            }
        }

        return pathInfo;
    }

    /**
     * {@inheritDoc}
     *
     * Simply remove the prefixed timestamp (by regular expression {@link #REVISION_TIMESTAMP_PATH_REGEX}) from the URI path info to resolve the
     * resource node.
     *
     * @param session session
     * @param pathInfo pathInfo
     * @return resource node
     */
    @Override
    public Node resolveToResourceNode(Session session, String pathInfo) {
        return super.resolveToResourceNode(session, REVISION_TIMESTAMP_PATTERN.matcher(pathInfo).replaceFirst(""));
    }

}
