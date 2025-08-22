package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 경험 생성 요청 DTO (ExperienceUpdate와 동일하지만 명확성을 위해 분리)
 */
public class ExperienceCreate {
    @SerializedName("experiences")
    private List<Experience> experiences;
    
    public ExperienceCreate(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExperienceCreate{");
        if (experiences != null) {
            sb.append("experiences=[");
            for (int i = 0; i < experiences.size(); i++) {
                Experience exp = experiences.get(i);
                sb.append("{contest_name=").append(exp.getContestName())
                  .append(", filter_id=").append(exp.getFilterId())
                  .append(", host_organization=").append(exp.getHostOrganization())
                  .append(", award_date=").append(exp.getAwardDate())
                  .append(", description=").append(exp.getDescription())
                  .append("}");
                if (i < experiences.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
